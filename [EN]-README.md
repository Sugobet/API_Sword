# WeChat official account：APT250
# [Burp New Classic Plugin] API Sword - Fully automatic depth collection of API interfaces in various responses

## Preface

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/388ed286aff845ce8863640b37d4636e.png)

Similar to many popular JavaScript and API mining tools like JS Finder and URLFinder, they are excellent tools. API Sword leverages Burp's capabilities and advantages.

Screenshot of the plugin's main page:

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/c23a7d7924924224810dc777c0e4e1bc.png)

## API Sword's main features

API Sword fully automatically prevents loops, extracting API and JS files within a certain range from various responses. It then performs deep API extraction and proactively requests valuable API, JS, and other files.

API results are WYSIWYG. The window on the right displays the source JS file for the API, allowing you to instantly retrieve API parameter information from the JS file and then use Burp to test it with a single click of Ctrl+R.

It's not as complex as you might think. API Sword primarily reduces the repetitive, time-consuming, and tedious task of searching for JS files, APIs, and API parameters. **

1. API Sword captures traffic within the Burp scope and extracts the majority of links from the HTTP responses.**
2. API Sword cleans any links and paths extracted in the previous step and, after verification, initiates GET and POST requests to APIs, JavaScript, and other applications.**
3. API Sword further processes the response to the previous active request, extracting information from the response and repeating the previous step. **API Sword has anti-loop functionality, eliminating the need to worry about infinite request loops**
4. API Sword pushes all matching API requests and responses, as well as the JavaScript file responses from the API interface, to the API Sword's Burp GUI.
5. API Sword automatically adds all relevant requests to Burp's target sitemap. **You can fully benefit from API Sword's benefits through features like analysis in the target sitemap.**

Users simply enable API Sword and set a "reasonable scope." Then, continue clicking various web system functions in their browser. All traffic will flow through Burp and ultimately be analyzed and processed by API Sword, which will then return the desired Devil Fruit.

**Considering operational security risks such as OPSEC, API Sword currently does not actively fuzz parameters. If there is a need, it will be added as an optional feature in the future.**

## API Sword Settings

In the Scope tab, we can set the scope

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/d99fe948bccf4783b1a04ea10fed64be.png)

This range is particularly important and it is recommended to consider it carefully, otherwise it is easy to scan into outer space.

After setting the range, let’s look at the Setting tab.

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/bad9daf34c75401d886b0a4101b02534.png)

1. Allow active API requests

This option is on by default and is not recommended, as it will prevent API Sword from extracting deeper data.

2. Use original headers

This option is on by default. If you want to specifically test unauthorized API endpoints, you can turn this option off. This will prevent any cookies or session information from being carried over.

3. Immediately stop all requests

This option is off by default to prevent unexpected pauses. It is recommended to use this option in conjunction with the first option.

4. Clear all data in the current SiteMap

This button clears all site data in the API Sword SiteMap.

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/d7e8eca0e2994a65b9bae2abb8554e69.png)

Other settings are under development and improvement. If you have any ideas, suggestions or questions, please submit an issue on GitHub.

## Acknowledgements

Thanks my `NSFOCUS mentor`

Thanks `mil1ln`

Thanks to everyone above for all your support of API Sword!

## TODO

1. Collect a M-KING polo shirt ⬛️
2. Added optional base URL path fuzzing ✅
3. Added custom response code filtering ✅
4. Added the option to customize the base path when making API Sword active requests ✅
5. Optimized the matching strategy, unlocking 80% of API Sword performance ✅
6. Optimized API Sword active requests to prevent access to dangerous APIs ⬛️
7. Fixed an issue where Burp default headers did not include the CT field ✅
8. Optimized the tag width in the response table ✅
9. Fixed a flickering UI issue in the sitemap ✅
10. Added optional custom request headers ✅
11. Add automatic sorting of tags in response list ⬛️
12. Optimize matching strategy ⬛️
13. Add depth control to alleviate burp jams when the volume is large ⬛️
