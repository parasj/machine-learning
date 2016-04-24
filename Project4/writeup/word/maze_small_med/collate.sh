#!/bin/bash
SMALL_RESULTS="../small_ql.txt"
MED_RESULTS="../med_ql.txt"

parse () {
    IF=$1
    OF=$2
    R=$(grep reward $IF | awk '{split($0,a," "); print a[2]}')
    I=$(grep iterations $IF | awk '{split($0,a," "); print a[2]}')
    T=$(grep iterations $IF | awk '{split($0,a," "); print a[5]}')
    G=$(grep Gamma $IF | awk '{split($0,a," "); print a[2]}')
    E=$(grep Epsilon: $IF | awk '{split($0,a," "); print a[2]}')
    echo "$G,$E,$I,$T,$R" >> $OF
}


pushd 'results/small'
for f in ql_*; do
    parse $f $SMALL_RESULTS
done
popd
pushd 'results/medium'
for f in ql_*; do
    parse $f $MED_RESULTS
done
popd
