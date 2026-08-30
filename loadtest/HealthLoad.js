import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        load: {
            executor: 'constant-arrival-rate',
            rate: 5000,
            timeUnit: '1s',
            duration: '30s',
            preAllocatedVUs: 200,
            maxVUs: 2000,
        },
    },
};

export default function () {

    const response = http.get(
        'http://localhost:8080/api/v1/products/health/load'
    );

    check(response, {
        'status is 200': (r) => r.status === 200,
    });
}