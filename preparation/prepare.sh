#!/bin/sh
set -e

hafas_path=$1
data_path=$2
temp_path=$3
output_path=$4

# First, convert T603
mkdir ${temp_path}/t603
sh t603/prepare.sh ${data_path}/t603.pdf ${temp_path}/t603
python3 t603/clean.py ${temp_path}/t603 ${output_path}/t603.csv
rm -r ${temp_path}/t603

# Second, convert bold stations from T603
python3 t603_bold/convert.py ${data_path}/t603_bold.raw.txt ${output_path}/t603_bold.txt

# Third, convert T651
mkdir ${temp_path}/t651
sh t651/convert_pdf.sh ${data_path} ${temp_path}/t651
python3 t651/process.py ${hafas_path} ${temp_path}/t651 ${output_path}
#rm -r ${temp_path}/t651
