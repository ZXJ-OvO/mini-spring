欢迎${uname}来参加我的提车庆典！
<#--这是个注释-->

<#--List集合输出-->
<#list userList as u>
    ${u.name} 今年 ${u.age} 岁！
</#list>

<#--Map输出 方式1 -->
${dmp.name}  在 ${dmp.city}


<#--Map输出 方式2 -->
<#list dmp?keys as k>
    ${dmp[k]}---
</#list>


<#--if判断-->
<#if (age>24)>
    大叔
<#else>
    小娃娃
</#if>

<#--空值处理-->
<#if address??>
    判断输出：${address}
</#if>
不判断输出(如果为空，给个默认值)：${address!"武汉"}


<#--内建函数-->
userList的长度是${userList?size}

<#--时间输出-->
${now?string('yyyy-MM-dd HH:mm:ss')}
${now?date}
${now?time}
${now?datetime}


<#--显示数字-->
钱：${money}
ID：${id?c}


<#--定义JSON串-->
<#assign text="{'bank':'工商银行','account':'10101920201920212'}" />
<#assign data=text?eval />
${data.bank}
${data.account}