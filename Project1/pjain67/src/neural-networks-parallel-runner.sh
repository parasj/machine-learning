#!/bin/bash

xargs --arg-file=nnrunner.sh \
      --max-procs=4  \
      --replace \
      --verbose \
      /bin/sh -c "{}"