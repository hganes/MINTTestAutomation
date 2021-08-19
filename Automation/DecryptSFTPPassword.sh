#!/bin/bash
id
sudo su  - $1  <<EOF
echo Switched to $1 user
id
cd /apps/mftdev/IBM/SIv6/bin
sh decrypt_string.sh $2
EOF

