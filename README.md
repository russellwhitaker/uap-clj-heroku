# uap-clj-heroku

A [Heroku](http://www.heroku.com)-deployable web app wrapping the [uap-clj useragent parser](https://github.com/russellwhitaker/uap-clj).

Features:
* POST useragent lookups for `os`, `browser`, `device`, or all fields;
* POST single or multiple useragent strings for lookup;
* Cookie-backed session store mode;
* Stack traces in development mode;
* Environment-based config via [environ](https://github.com/weavejester/environ)

## Usage

To start a local web server for testing from the command line:

```bash
$ lein run -m uap-clj-heroku.web
2017-01-03 14:01:23.938:INFO::main: Logging initialized @3025ms
2017-01-03 14:01:24.362:INFO:oejs.Server:main: jetty-9.2.10.v20150310
2017-01-03 14:01:24.395:INFO:oejs.ServerConnector:main: Started ServerConnector@8f62f9a{HTTP/1.1}{0.0.0.0:5000}
2017-01-03 14:01:24.395:INFO:oejs.Server:main: Started @3482ms
```

Or, you can start the server manually in the REPL:

```bash
lein repl
nREPL server started on port 58045 on host 127.0.0.1 - nrepl://127.0.0.1:58045
REPL-y 0.3.7, nREPL 0.2.12
Clojure 1.8.0
Java HotSpot(TM) 64-Bit Server VM 1.8.0_102-b14
    Docs: (doc function-name-here)
          (find-doc "part-of-name-here")
  Source: (source function-name-here)
 Javadoc: (javadoc java-object-or-class-here)
    Exit: Control+D or (exit) or (quit)
 Results: Stored in vars *1, *2, *3, an exception in *e

user=> (use 'uap-clj-heroku.web)
2017-01-03 14:03:24.543:INFO::nREPL-worker-0: Logging initialized @16226ms
nil
user=> (defonce server (-main))
2017-01-03 14:03:39.867:INFO:oejs.Server:nREPL-worker-0: jetty-9.2.10.v20150310
2017-01-03 14:03:39.900:INFO:oejs.ServerConnector:nREPL-worker-0: Started ServerConnector@1219e226{HTTP/1.1}{0.0.0.0:5000}
2017-01-03 14:03:39.901:INFO:oejs.Server:nREPL-worker-0: Started @31582ms
#'user/server
user=> (.stop server)
2017-01-03 14:03:46.028:INFO:oejs.ServerConnector:nREPL-worker-0: Stopped ServerConnector@1219e226{HTTP/1.1}{0.0.0.0:5000}
nil
user=> (.start server)
2017-01-03 14:03:49.889:INFO:oejs.Server:nREPL-worker-0: jetty-9.2.10.v20150310
2017-01-03 14:03:49.890:INFO:oejs.ServerConnector:nREPL-worker-0: Started ServerConnector@1219e226{HTTP/1.1}{0.0.0.0:5000}
2017-01-03 14:03:49.890:INFO:oejs.Server:nREPL-worker-0: Started @41572ms
nil
user=>
```

Initialize a git repository for your project.

    $ git init
    $ git add .
    $ git commit -m "Initial commit."

You'll need the [heroku toolbelt](https://toolbelt.herokuapp.com) installed to manage the heroku side of your app. Once it's installed, create the app on Heroku:

```bash
$ heroku apps:create my-ua-parser
```

Then you can deploy to Heroku:

    $ git push heroku master
    Writing objects: 100% (13/13), 2.87 KiB, done.
    Total 13 (delta 0), reused 0 (delta 0)

    -----> Heroku receiving push
    -----> Clojure app detected
    -----> Installing Leiningen
           Downloading: leiningen-2.0.0-preview7-standalone.jar
    [...]
    -----> Launching... done, v3
           http://uap-clj-heroku.herokuapp.com deployed to Heroku

    To git@heroku.com:uap-clj-heroku.git
     * [new branch]      master -> master

Test it with `curl`:

```bash
$ curl http://uap-clj-heroku.herokuapp.com
Useragent parser v1.3.1
```

You can send single POST requests containing queries for any of full `useragent`, `os`, `device`, or `browser`. It's easiest with JSON payloads to source from a file, e.g. with contents like:

```JSON
{"queries":
  [{"ua":"Lenovo-A288t_TD/S100 Linux/2.6.35 Android/2.3.5 Release/02.29.2012 Browser/AppleWebkit533.1 Mobile Safari/533.1 FlyFlow/1.4",
    "lookup":"useragent"},
   {"ua":"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.19) Gecko/2010031218 FreeBSD/i386 Firefox/3.0.19,gzip(gfe),gzip(gfe)",
    "lookup":"browser"},
   {"ua":"Mozilla/5.0 (Linux; U; Android 4.0.3; en-us; Amaze_4G Build/IML74K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
    "lookup":"device"},
   {"ua":"UCWEB/2.0 (Linux; U; Adr 2.3.6; en-US; HUAWEI_Y210-0251) U2/1.0.0 UCBrowser/8.6.0.318 U2/1.0.0 Mobile",
    "lookup":"os"}]}
```

```bash
curl -H "Content-Type: application/json" -X POST -d @query.json  http://uap-clj-heroku.herokuapp.com/useragent
{"results":[{"ua":"Lenovo-A288t_TD/S100 Linux/2.6.35 Android/2.3.5 Release/02.29.2012 Browser/AppleWebkit533.1 Mobile Safari/533.1 FlyFlow/1.4","browser":{"family":"Baidu Explorer","major":"1","minor":"4","patch":""},"os":{"family":"Android","major":"2","minor":"3","patch":"5","patch_minor":""},"device":{"family":"Lenovo A288t_TD","brand":"Lenovo","model":"A288t_TD"}},{"browser":{"family":"Firefox","major":"3","minor":"0","patch":"19"},"ua":"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.19) Gecko/2010031218 FreeBSD/i386 Firefox/3.0.19,gzip(gfe),gzip(gfe)"},{"device":{"family":"HTC Amaze 4G","brand":"HTC","model":"Amaze 4G"},"ua":"Mozilla/5.0 (Linux; U; Android 4.0.3; en-us; Amaze_4G Build/IML74K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"},{"os":{"family":"Android","major":"2","minor":"3","patch":"6","patch_minor":""},"ua":"UCWEB/2.0 (Linux; U; Adr 2.3.6; en-US; HUAWEI_Y210-0251) U2/1.0.0 UCBrowser/8.6.0.318 U2/1.0.0 Mobile"}]}
```

The cookie-backed session store needs a session secret configured for encryption:

    $ heroku config:add SESSION_SECRET=$RANDOM_16_CHARS

__Maintained by Russell Whitaker__

## License

The MIT License (MIT)

Copyright (c) 2017 Russell Whitaker

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
