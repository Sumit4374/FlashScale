import http from 'k6/http';

export const options = {
    scenarios: {
        distributed_stampede: {
            executor: 'constant-arrival-rate',
            rate: 1000,
            timeUnit: '1s',
            duration: '1s',
            preAllocatedVUs: 100,
            maxVUs: 1000,
        },
    },
};

export default function () {

    const url =
        `http://localhost:8080/api/v1/products/get/1`;

    const response = http.get(url);

    if (response.status !== 200) {
        console.error(`HTTP ${response.status}`);
    }
}