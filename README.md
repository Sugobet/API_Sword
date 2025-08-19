[\[EN-ReadMe\]](https://github.com/Sugobet/API_Sword/blob/main/%5BEN%5D-README.md)
# 微信公众号：APT250
# [burp新经典插件] API剑 -  全自动深度 收集各种响应中的API接口

开始之前，很抱歉我推迟了联合锻剑计划的时间，工作后的时间比较有限，我也在寻找一个时机，尽量早日推出。

API剑这个插件灵感来源于实习中，API剑处于测试中，近期不打算开源，后面功能完善后会考虑开源交流学习

	[API剑]项目地址：https://github.com/Sugobet/API_Sword

jar包在release
## 前言

这个插件结合了我近期的工作内容和此前我的4万美刀赏金微软账户漏洞api的部分经验

“API剑”这个burp插件耗时一周加文章发布日前一晚的一个通宵，连夜做出了测试版，我从java完全零基础 + 极度讨厌java，到翻看burp插件官方实例 + 不断的debug，凭借我已久的通用编程思想和编码经验，还是把它做了出来，目前一千行代码左右，比较简陋，望见谅，望包涵。

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/388ed286aff845ce8863640b37d4636e.png)

与众多JS Finder、URLFinder等比较火热的相关js、api挖掘工具类似，它们是非常优秀的工具，**而API剑凭借burp的特点而获得能力和优势。**

插件主页面截图：

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/c23a7d7924924224810dc777c0e4e1bc.png)

## API剑的主要功能

API剑 全自动防环路，从各种响应里提取范围内的api和js文件，然后深度提取api，主动请求api、js等有价值文件

api结果所见即所得，右边的窗口显示api的来源js，可以立刻从js里面获得api的参数信息，然后burp再ctrl + r一键过去测

它没有想象的那么复杂，API剑做的事情更多是为我们**减少了大量重复耗时且无趣的js、api、api参数搜寻工作。**

1. API剑捕获经过burp的范围内的流量，并从**http响应中提取绝大多数link**
2. API剑将对上一步提取的任意链接、路径进行清洗，并由**API剑判断后对API、JS等主动发起GET、POST请求**
3. API剑对上一步主动请求的响应进一步的处理，继续从响应中提取信息，并重复上一步的动作，**API剑具有防环路功能，无需担心死循环请求问题**
4. API剑对所有符合条件的API请求、响应，以及该API接口来源的js文件响应，全部推送到API剑的burp GUI中
5. API剑自动将所有相关请求添加至burp的target sitemap中，**您可在target的sitemap的分析等功能中尽情享受API剑带来的果实**

用户只需要启用API剑并设置一个“合理的范围”，接着在浏览器中继续点击web系统的各种功能，让所有流量经过burp，最终交给API剑做分析处理，API剑将会向您返回您想要的恶魔果实。

**考虑到opsec等操作安全风险，目前API剑不会主动fuzz参数，如果后续有需求再额外添加作为可选功能。**

## API剑的设置

在Scope选项卡中，我们可以设置范围

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/d99fe948bccf4783b1a04ea10fed64be.png)

这个范围特别重要，建议谨慎考虑，否则容易扫到外太空去。

设置好范围后我们再看Setting选项卡

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/bad9daf34c75401d886b0a4101b02534.png)

1. 允许主动对API请求

这个选项默认开，不建议关，否则API剑无法更深层提取数据

2. 是否使用原headers

默认开，如果想专门测试未授权api接口，可以把这个选项关掉，关掉后不会携带任何cookie或session等信息

3. 立即停止发送所有请求

默认关，避免遇到突发情况想暂停，用来刹车的，建议搭配第一个选项一起使用

4. 清除当前SiteMap所有数据

这个按钮用于清除API剑的Site Map中的所有站点数据

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/d7e8eca0e2994a65b9bae2abb8554e69.png)

其它设置待开发和完善，如有任何想法建议和问题，可通过github上提issue反馈

## 致谢

感谢 我的`绿盟导师`

感谢 ` mil1ln`

感谢以上所有人为API剑提供的一切支持！

## TODO

1. 收集一件梅花K的polo衫 ⬛️
2. 添加可选的base url路径fuzz ✅
3. 添加自定义响应码过滤 ✅
4. 添加API剑主动请求时，添加自定义base路径的选项 ✅
5. 优化了匹配策略，解锁API剑性能80% ✅
6. API剑主动请求优化，避免访问危险api ⬛️
7. 解决burp默认header不携带CT字段的问题 ✅
8. 优化响应table的tags宽度 ✅
9. 修复sitemap的ui闪烁问题 ✅
10. 添加自定义请求头可选功能 ✅
11. 添加响应列表的tags自动排序 ⬛️
12. 优化匹配策略 ⬛️
13. 增加深度控制，缓解量大时burp卡顿 ⬛️
