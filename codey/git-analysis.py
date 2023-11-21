import os

os.chdir('./clone/')
os.system('ls -lrt')
os.system('git diff main...HEAD  --output ../diff.txt')
os.system('cat /workspace/diff.txt')