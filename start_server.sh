#!/bin/bash

mongod --fork --logpath ./mongo.log

lein ring server

