import http from 'k6/http';
import { check } from 'k6';

export const options = {
    summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)'],
    stages: [
        { duration: '30s', target: 10 },
        { duration: '30s', target: 50 },
        { duration: '30s', target: 100 },
        { duration: '30s', target: 250 },
        { duration: '30s', target: 500 },
        { duration: '30s', target: 1000 },
        { duration: '30s', target: 0 },
    ],
};

export default function () {
    const response = http.get(
        'http://localhost:8080/api/v1/products/get/1'
    );

    check(response, {
        'status is 200': (r) => r.status === 200,
    });
}