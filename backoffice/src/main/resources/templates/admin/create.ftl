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
<#assign title=tr.title_admin_create>
<#include "../swap-files/top-bar.ftl">

<div class="container">
    <form action="create" method="POST">
        <div class="row">
            <div class="input-field col s12 m6 offset-m3">
                <input name="login_name" id="login_name" type="text" autofocus/>
                <label for="login_name">Login name</label>
            </div>
            <div class="input-field col s12 m6 offset-m3">
                <input name="login_password" id="login_password" type="password"/>
                <label for="login_password">Password</label>
            </div>
            <div class="input-field col s12 m6 offset-m3">
                <input name="login_password_repeat" id="login_password_repeat" type="password"/>
                <label for="login_password_repeat">Password (repeat)</label>
            </div>
            <div class="input-field col s12 m6 offset-m3">
                <button class="btn waves-effect waves-light green" type="submit" name="action">Create New Backoffice Account
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