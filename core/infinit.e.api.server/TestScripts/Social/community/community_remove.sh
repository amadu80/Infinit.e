################################################################################
# community_remove.sh
################################################################################
# Description:
# 
# Params
# $1 = API Server
# $2 = username
# $3 = password
# $4 = community id
################################################################################
echo ''
echo 'Log in to Infinit.e and get a cookie'
echo curl -XGET -c cookie.txt  $1/auth/login/$2/$3
echo ''
curl -XGET -c cookie.txt  $1/auth/login/$2/$3
echo ''
echo ''

echo 'Command:'
echo curl -XGET -b cookie.txt $1/social/community/remove/$4
echo ''
echo 'Response:'
curl -XGET -b cookie.txt $1/social/community/remove/$4
echo ''
echo ''











