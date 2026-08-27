import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        stampede: {
            executor: 'constant-arrival-rate',
            rate: 20000,
            timeUnit: '1s',
            duration: '1s',

            preAllocatedVUs: 1000,
            maxVUs: 1000,
        },
    },
};

export default function () {
    const response = http.get(
        'http://localhost:8080/api/v1/products/get/1'
    );

    check(response, {
        'status is 200': (r) => r.status === 200,
    });
}