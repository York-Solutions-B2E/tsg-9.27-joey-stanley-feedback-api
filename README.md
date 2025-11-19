# Feedback API

This is a full-stack capstone project for TSG (weeks 7 and 8).

## Authors

- Stanley Aviles
- Joey Cramsey

## Full Run Procudure

First, find a folder of your choosing to download all three repos.

In this folder, run the following commands:
```bash
git clone https://github.com/York-Solutions-B2E/tsg-9.27-joey-stanley-frontend-feedback-ui.git
git clone https://github.com/York-Solutions-B2E/tsg-9.27-joey-stanley-feedback-api.git
git clone https://github.com/York-Solutions-B2E/tsg-9.27-joey-stanley-feedback-analytics-consumer.git
```

To run all three repos in one batch, run:

```bash
cd tsg-9.27-joey-stanley-feedback-api
docker compose -f masterswitch.yml up
```

## Run API In Isolation

To run *just* the API (spring boot, kafka, postgres), run the following:

```bash
docker compose -f solo.yml up
```

## Tests

All tests are run within the api and consumer docker containers, as part of the spin-up process.

Logs for tests are found in `logs/test-results.log`.

**NOTE:** This file does not get wiped between tests, so you might be seeing previous results in there, too!
