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
<#assign title=tr.title_user_details>
<#include "../swap-files/top-bar.ftl">

<div class="container">
    <br>
    <a href="/backoffice/backoffice/user/list/10/1" class="waves-effect waves-light btn green">
        <i class="material-icons left">
            keyboard_arrow_left
        </i>
        back
    </a>
    <ul class="collection with-header">
        <li class="collection-header"><h4>User #${id}</h4></li>
        <li class="collection-item"><b>Id:</b> ${id}</li>
        <li class="collection-item"><b>E-Mail:</b> ${name}</li>
    </ul>
    <form action="/backoffice/backoffice/user/delete/${id}" method="POST">
        <button class="waves-effect waves-light btn green">
            <i class="material-icons left">
                delete
            </i>
            Delete Entry
        </button>
    </form>
</div>

<!--JavaScript at end of body for optimized loading-->
<#include "../swap-files/script-imports.ftl">
</body>
</html>
