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
<#assign title=tr.title_admin_changePw>
<#include "../swap-files/top-bar.ftl">

<div class="container">
    <a href="/backoffice/backoffice/admin/details/${name}" class="waves-effect waves-light btn green"><i class="material-icons left">keyboard_arrow_left</i>back</a>
    <form action="/backoffice/backoffice/admin/changePw" method="POST">
        <div class="row">
            <div class="input-field col s12 m6 offset-m3">
                <input name="login_name" id="login_name" value="${name}" type="text"/>
                <label for="login_name">Login name</label>
            </div>
            <div class="input-field col s12 m6 offset-m3">
                <input name="new_password" id="login_password_repeat" type="password" autofocus/>
                <label for="new_password">Password (new)</label>
            </div>
            <div class="input-field col s12 m6 offset-m3">
                <input name="new_password_repeat" id="login_password_repeat" type="password"/>
                <label for="new_password_repeat">Password (new repeat)</label>
            </div>
            <div class="input-field col s12 m6 offset-m3">
                <button class="btn waves-effect waves-light green" type="submit" name="action">Change Password
                    <i class="material-icons right"></i>
                </button>
            </div>

        </div>
    </form>
</div>

<!--JavaScript at end of body for optimized loading-->
<#include "../swap-files/script-imports.ftl">
</body>
</html>
