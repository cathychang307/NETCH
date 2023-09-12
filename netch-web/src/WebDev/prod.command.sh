compass clean -c netch-web/config.rb netch-web/ && compass compile -c netch-web/config.rb netch-web/

WORKDIR=/Users/shawnyhw6n9/Documents/myProject/BOT_NETCH/netch/netch-web/src/main/webapp/static
cd $WORKDIR
node ../static/requirejs/2.3.2/r.js -o build.js optimize=uglify name=main baseUrl=../static out=../static/main.min.js
