<!DOCTYPE html>
<#import "../lang/translations_en.ftl" as tr>
<html lang="${.lang}">
<head>
    <meta charset="UTF-8">
    <title>BLELoc Admin Interface</title>
    <#include "../swap-files/css-imports.ftl">

    <!--Let browser know website is optimized for mobile-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
</head>

<body>
<#assign title=tr.title_admin_list>
<#include "../swap-files/top-bar.ftl">

<div class="container">
    <table class="highlight ">
        <thead>
        <tr>
            <th>id</th>
            <th>Login name</th>
            <th>Details</th>
        </tr>
        </thead>

        <tbody>
        <#list accounts as a>
            <tr>
                <td>${a.id}</td>
                <td>${a.name}</td>
                <td><a href="/backoffice/backoffice/admin/details/${a.name}"><i class="material-icons green-text">send</i></a></td>
            </tr>
        </#list>
        </tbody>
    </table>

    <ul class="pagination center-align">
        <#if 1 < page>
            <li class="waves-effect"><a href="/backoffice/backoffice/admin/list/${size}/${page-1}">
        <#else>
            <li class="disabled"><a>
        </#if>
            <i class="material-icons">chevron_left</i>
        </a></li>

        <#macro pageItem pageNo>
            <#if page = pageNo>
                <li class="active green">
            <#else>
                <li class="waves-effect">
            </#if>
                <a href="/backoffice/backoffice/admin/list/${size}/${pageNo}">${pageNo}</a>
            </li>
        </#macro>

        <#-- min(x,y) = (x+y-abs(x-y))/2 ; max(x,y) = (x+y+abs(x-y))/2 -->
        <#assign selectionRange=2>
        <#assign rangeStart=((page-selectionRange+2+(page-selectionRange-2)?abs)/2)?int>
        <#assign rangeEnd=((pageCount-1+page+selectionRange-(pageCount-1-page-selectionRange)?abs)/2)?int>

        <@pageItem pageNo=1/>
        <#if 2 < rangeStart>
            <li class="disabled"><a>-</a></li>
        </#if>
        <#if rangeStart <= rangeEnd>
            <#list rangeStart..rangeEnd as i>
                <@pageItem pageNo=i/>
            </#list>
        </#if>
        <#if 2 <= pageCount>
            <#if rangeEnd < pageCount - 1>
                <li class="disabled"><a>-</a></li>
            </#if>
            <@pageItem pageNo=pageCount/>
        </#if>

        <#if page < pageCount>
            <li class="waves-effect"><a href="/backoffice/backoffice/admin/list/${size}/${page+1}">
        <#else>
            <li class="disabled"><a>
        </#if>
            <i class="material-icons">chevron_right</i>
        </a></li>
    </ul>

    <a href="/backoffice/backoffice/admin/create">
        <div class="input-field col s12 m6 offset-m3">
            <button class="btn waves-effect waves-light green" type="submit" name="action">Create new Backoffice Account
                <i class="material-icons right">send</i>
            </button>
        </div>
    </a>


</div>

<!--JavaScript at end of body for optimized loading-->
<#include "../swap-files/script-imports.ftl">
</body>
</html>