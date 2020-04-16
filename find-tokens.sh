if git grep -e api_sandbox --or -e api_live --or -e sdk_sandbox --or -e sdk_live -- ':!find-tokens.sh'
then
  echo "ERROR: Found a token in your code. Please remove it before commiting."
  exit 1
fi