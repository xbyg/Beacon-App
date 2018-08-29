# Overview
This is an Android application written in Kotlin and developed for [Beacon](https://www.beacon.com.hk/2011/index.php). Its aim is to provide a concise user interface and a more convenient way to use the funtions listed below!  

### Functions
* Viewing the introductions and courses of all Beacon tutors
* Viewing and exchanging lessons after logging in Beacon account
* Viewing locations of Beacon branch schools

More is coming......

### How does it work?
This app makes http requests to Beacon server and scrapes informations from HTML from server's response. Then, displays these items.

_**P.S. Whether user's password is correct or not is determined by whether the http status code is 302 (A status code that asks for redirecting url)**_ 


# Alpha version
It took me around one month to build this app but it is still not stable:poop:. Therefore, the app may crash and if so it will automatically send crash report to console(doesn't contain personal informations). If you want to try, you may [click here](https://github.com/xbyg/Beacon-App/releases) and click app-pre-release-[version].apk to install the app!


# Demo
<img src="https://github.com/xbyg/Beacon-App/blob/master/images/github_logo.gif" width="250"><img src="https://github.com/xbyg/Beacon-App/blob/master/images/github_tutors.gif" width="250">
<img src="https://github.com/xbyg/Beacon-App/blob/master/images/github_intro.gif" width="250">
<img src="https://github.com/xbyg/Beacon-App/blob/master/images/github_exchange.gif" width="250">
<img src="https://github.com/xbyg/Beacon-App/blob/master/images/github_locations.gif" width="250">
