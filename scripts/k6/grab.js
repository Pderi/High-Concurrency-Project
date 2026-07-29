import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

/**
 * 本地压测抢票接口（需先启动 tkt-server，并准备可售场次/票档）
 *
 * 用法示例：
 *   k6 run -e BASE_URL=http://localhost:8080 -e TIER_ID=1 -e SESSION_ID=1 -e USER_ID=1001 scripts/k6/grab.js
 *
 * 压测前建议将 tkt.rate-limit.limit-for-period 调高或 enabled=false，否则会大量 RATE_LIMIT。
 */

const rateLimited = new Rate('grab_rate_limited');
const soldOut = new Rate('grab_sold_out');
const success = new Rate('grab_success');

export const options = {
  scenarios: {
    grab_peak: {
      executor: 'constant-arrival-rate',
      rate: 50,
      timeUnit: '1s',
      duration: '30s',
      preAllocatedVUs: 20,
      maxVUs: 100,
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.3'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const SESSION_ID = Number(__ENV.SESSION_ID || 1);
const TIER_ID = Number(__ENV.TIER_ID || 1);
const USER_ID = Number(__ENV.USER_ID || 1001);

export default function () {
  const idempotencyKey = `k6-${__VU}-${__ITER}-${Date.now()}`;
  const payload = JSON.stringify({
    sessionId: SESSION_ID,
    tierId: TIER_ID,
    quantity: 1,
    idempotencyKey: idempotencyKey,
  });
  const res = http.post(`${BASE_URL}/app-api/tkt/order/grab`, payload, {
    headers: {
      'Content-Type': 'application/json',
      'X-User-Id': String(USER_ID + (__VU % 50)),
      'X-Trace-Id': `k6-${__VU}-${__ITER}`,
    },
    timeout: '5s',
  });

  let body = {};
  try {
    body = res.json();
  } catch (e) {
    body = {};
  }
  const code = body.code;

  check(res, {
    'status is 200': (r) => r.status === 200,
  });

  success.add(code === 0);
  rateLimited.add(code === 1010005001);
  soldOut.add(code === 1010003002);

  sleep(0.01);
}
