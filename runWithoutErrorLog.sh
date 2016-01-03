#!/bin/sh

sbt run 2>&1 | grep -v "error"

