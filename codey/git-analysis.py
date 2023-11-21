import os

os.chdir('./clone/')
os.system('ls -lrt')
os.system('git diff main...fix  --output ../diff.txt')
os.system('cat /workspace/diff.txt')