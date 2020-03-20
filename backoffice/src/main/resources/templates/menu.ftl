<!DOCTYPE html>
<#import "lang/translations_en.ftl" as tr>
<html lang="${.lang}">
<head>
    <meta charset="UTF-8">
    <title>BLELoc Admin Interface</title>
    <#include "swap-files/css-imports.ftl">

    <!--Let browser know website is optimized for mobile-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
</head>

<body>
<#assign title=tr.title_menu>
<#include "swap-files/top-bar.ftl">

<div class="container">
        <div class="row">
            <div class="input-field col s12 m6 offset-m3">
                <ul class="collection">
                    <li class="collection-item"><a href="/backoffice/backoffice/admin/list/10/1" class="grey-text text-darken-4"><i class="material-icons left">account_circle</i>Administrators</a></li>
                    <li class="collection-item"><a href="/backoffice/backoffice/user/list/10/1" class="grey-text text-darken-4"><i class="material-icons left">group</i>Users</a></li>
                    <!--<li class="collection-item"><a href="" class="grey-text text-darken-4"><i class="material-icons left">vpn_key</i>Access Tokens</a></li>-->
                    <li class="collection-item"><a href="/backoffice/backoffice/stats" class="grey-text text-darken-4"><i class="material-icons left">insert_chart</i>Statistics Data</a></li>
                    <li class="collection-item"><a href="/backoffice/backoffice/settings" class="grey-text text-darken-4"><i class="material-icons left">settings</i>Settings Data</a></li>
                    <li class="collection-item"><form method="POST" action="/backoffice/backoffice/auth/logout"><button style="padding:0;border:none;background:inherit" class="grey-text text-darken-4"><i class="material-icons left">power_settings_new</i>Logout</button></form></li>
                </ul>
            </div>
        </div>
</div>

<!--JavaScript at end of body for optimized loading-->
<#include "swap-files/script-imports.ftl">
</body>
</html>
