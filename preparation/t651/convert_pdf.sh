#!/bin/sh

data_path=$1
output_path=$2

# AWelle
pdf-stapler sel ${data_path}/t651.awelle.pdf 64-80 ${output_path}/awelle.temp.pdf
pdftotext ${output_path}/awelle.temp.pdf ${output_path}/t651.awelle.txt

# Engadin Mobil
pdf-stapler sel ${data_path}/t651.engadin_mobil.pdf 24-27 ${output_path}/engadin_mobil.temp.pdf
pdftotext -layout ${output_path}/engadin_mobil.temp.pdf ${output_path}/t651.engadin_mobil.txt

# Libero
pdf-stapler sel ${data_path}/t651.libero.pdf 47-67 ${output_path}/libero.temp.pdf
pdftotext ${output_path}/libero.temp.pdf ${output_path}/t651.libero.txt

# Ostwind
pdf-stapler sel ${data_path}/t651.ostwind.pdf 56-136 ${output_path}/ostwind.temp.pdf
pdftotext -layout ${output_path}/ostwind.temp.pdf ${output_path}/t651.ostwind.txt

# Passepartout
pdf-stapler sel ${data_path}/t651.passepartout.pdf 65-78 ${output_path}/passepartout.temp.pdf
pdftotext ${output_path}/passepartout.temp.pdf ${output_path}/t651.passepartout.txt

# TransReno
pdf-stapler sel ${data_path}/t651.transreno.pdf 38-41 ${output_path}/transreno.temp.pdf
pdftotext -layout ${output_path}/transreno.temp.pdf ${output_path}/t651.transreno.txt

# Frimobil
pdf-stapler sel ${data_path}/t651.frimobil.pdf 36-58 ${output_path}/frimobil.temp.pdf
pdftotext -layout ${output_path}/frimobil.temp.pdf ${output_path}/t651.frimobil.txt

# TVSZ
cp ${data_path}/t651.tvsz.docx ${output_path}/t651.tvsz.docx

# TVZG
pdf-stapler sel ${data_path}/t651.tvzg.pdf 70-82 ${output_path}/tvzg.temp.pdf
pdftotext -layout ${output_path}/tvzg.temp.pdf ${output_path}/t651.tvzg.txt

# Unireso
pdf-stapler sel ${data_path}/t651.unireso.pdf 69-95 ${output_path}/unireso.temp.pdf
pdftotext -layout ${output_path}/unireso.temp.pdf ${output_path}/t651.unireso.txt

# ZVV§
pdf-stapler sel ${data_path}/t651.zvv.pdf 2-101 ${output_path}/zvv.temp.pdf
pdftotext -layout ${output_path}/zvv.temp.pdf ${output_path}/t651.zvv.txt
