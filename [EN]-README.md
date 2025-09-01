# WeChat Official Account: APT250

# Adhering to the principles of open source and communication and learning, API Sword will be open sourced in two weeks (after September 7th) and will also be available on the Burp official plugin store (BApp Store) for easy updates and use.

# [Burp New Classic Plugin] API Sword - Fully automatic deep analysis of API interfaces in various responses

The JAR package is being released and will be available for download from the Burp official plugin store after it is available.

## Preface

This plugin combines my recent work and some of my previous experience with a $40,000 bounty for Microsoft account vulnerability APIs.

API Sword developers have used it to achieve numerous project successes and discover common 0-days. With this tool, I no longer have to manually search for any interface, path, or parameter in any JavaScript code.

<img width="584" height="526" alt="image" src="https://github.com/user-attachments/assets/b68064c3-69f4-47f2-a042-0164d976870a" />

Similar to many popular JavaScript and API mining tools like JS Finder and URLFinder, they are excellent tools. **API Sword, however, leverages Burp's capabilities and advantages.**

Screenshot of the plugin's main page:

<img width="3022" height="1564" alt="image" src="https://github.com/user-attachments/assets/efe5702a-a896-421a-8a67-c31f2869ff70" />

## API Sword's Main Features

API Sword automatically prevents loops, extracting API and JS files within a specified range from various responses. It then recursively extracts APIs deep within the API, proactively requesting valuable API, JS, and other files.

API results are WYSIWYG. The window on the right displays the source JS file for the API. You can immediately retrieve API parameter information from the JS file and then use Burp to test it with a single click of Ctrl+R.

It's not as complex as you might think. **API Sword primarily reduces the repetitive, time-consuming, and tedious task of searching for JS files, APIs, and API parameters.**

1. API Sword captures traffic within the Burp scope and extracts the majority of links from the HTTP responses.
2. API Sword cleans any links and paths extracted in the previous step and, after verification, initiates GET and POST requests to APIs, JavaScript, and other applications.
3. API Sword further processes the response to the previous active request, extracting information from the response and repeating the previous step. **API Sword has anti-loop functionality, eliminating the need to worry about infinite request loops**
4. API Sword pushes all matching API requests and responses, as well as the JavaScript file responses from the API interface, to the API Sword's Burp GUI.
5. API Sword automatically adds all relevant requests to Burp's target sitemap. **You can fully benefit from API Sword's benefits through features like analysis in the target sitemap.**

Users simply enable API Sword and set a "reasonable scope." Then, continue clicking various web system functions in their browser. All traffic will flow through Burp and ultimately be analyzed and processed by API Sword, which will then return the desired Devil Fruit.

**Due to operational security risks such as OPSEC, API Sword currently does not actively fuzz parameters. This feature will be added as an optional feature if needed.**

## How to use it?

Using API Sword is very simple.

1. Install the plugin in Burp version 2024 or later and ensure the plugin is running correctly.
2. Set the scope for the plugin.
3. Open a browser and ensure that browser traffic is flowing through Burp.
4. Visit the target website and click and test everything you see on the website.
5. After a while, check the results in the API Sword sitemap.

## API Sword Settings

In the Scope tab, we can set the scope. The scope can be a URL, domain name, or IP address.

<img width="1926" height="1118" alt="image" src="https://github.com/user-attachments/assets/bd26e692-8876-4d3a-8e22-c4a1a73856ad" />

This scope is extremely important and should be carefully considered, otherwise it could easily lead to scanning into outer space.

After setting the scope, let's look at the Settings tab.

<img width="3022" height="1368" alt="image" src="https://github.com/user-attachments/assets/18596668-b4cf-4a59-b603-2c1a4b15bab8" />

1. Allow active API requests

This option is on by default and is not recommended, as it will prevent API Sword from extracting deeper data.

2. Use original headers

This is on by default. If you want to specifically test unauthorized API endpoints, you can turn this option off. After turning it off, no cookies or session information will be carried.

3. Stop all requests immediately

This is off by default to prevent unexpected pauses. It is recommended to use this option in conjunction with the first option.

4. Clear all data in the current SiteMap

This button clears all data in the API Sword site. All site data in the Map

![Insert image description here](https://i-blog.csdnimg.cn/direct/d7e8eca0e2994a65b9bae2abb8554e69.png)

5. Enable active HTTP request rate

Limit the time between each request

6. Whether to add a custom path request to active requests

If this option is enabled, API Sword will add the specified custom path to the main URL before concatenation.

7. Filter out custom response codes other than 200

8. Allow API Sword to actively find the baseURL from the response and concatenate the path to the baseURL

9. Add custom header fields: (automatically overwrite existing header fields)

10. Enable bypassing dangerous interface access (skip interfaces containing strings)

11. Save scope and all settings

12. Add a custom path after the API interface and before the parameters

13. Control the number of threads

Other settings are under development and improvement. If you have any suggestions or questions, please submit an issue on GitHub.

## Acknowledgements

Thanks `My NSFOCUS mentor`

Thanks `mil1ln`

Thanks `everyone who provided valuable feedback and suggestions during the beta phase of API Sword.`

Thanks everyone for all your support!

## TODO

1. Collect a NSFOCUS M-KING polo shirt ⬛️
2. Add optional base URL path fuzzing ✅
3. Add custom response code filtering ✅
4. Add a custom base path option when adding API Sword active requests ✅
5. Optimized matching strategy, unlocking 80% of API Sword performance ✅
6. Optimized API Sword active requests to prevent access to dangerous APIs ✅
7. Fixed the issue where Burp default headers do not include the CT field ✅
8. Optimized the width of tags in the response table ✅
9. Fixed flickering UI issue in sitemap ✅
10. Add optional custom request headers ✅
11. Add automatic tag sorting in the response list ✅
12. Optimize matching strategy ✅
13. Optimize code blocks ⬛️
14. Register Burp uninstall handler ✅
15. Add readable code comments in both Chinese and English throughout the code ⬛️
16. Add Chinese and English language switching to the GUI ✅
17. Optimized the UI to address Burp lag and rendering issues ✅
18. Added the ability to save scopes and configurations ✅
19. Added the ability to control the active HTTP request rate ✅
20. Added multi-threading functionality ✅
21. Added custom path functionality after the interface and before the parameters ✅
22. Urgently fixed the issue of anti-dead loop logic failure caused by multithreading ✅
23. Fixed a display error issue with automatic sorting ✅
24. Optimized the API list UI so that arrow keys will no longer interrupt spell casting when inserting data ✅
25. Completely fix the bug that caused the anti-loop logic to still fail due to multithreading ✅
