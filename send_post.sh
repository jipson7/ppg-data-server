#!/bin/bash

curl -v -X POST -H "Content-Type: application/json" -d '{"key1":"value1", "key2":"value2"}' http://localhost:3000/trials
