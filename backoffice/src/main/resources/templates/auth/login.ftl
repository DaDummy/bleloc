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

<nav class="light-blue darken-2">
    <div class="nav-wrapper">
        <a href="#" class="brand-logo">&nbsp;${tr.title_login}</a>
    </div>
</nav>

<div class="container">
    <div class="row">
        <div class="input-field col s12 m6 offset-m3">
            <img class="center-align" src="/backoffice/img/BLELoc-Logo.png" width="100%"/>
        </div>
    </div>
    <form action="auth/login" method="POST">
    <div class="row">
        <div class="input-field col s12 m6 offset-m3">
            <input name="login_name" type="text" autofocus/>
            <label for="login_name">${tr.login_form_name}</label>
        </div>
        <div class="input-field col s12 m6 offset-m3">
            <input name="login_password" type="password"/>
            <label for="login_password">${tr.login_form_password}</label>
        </div>
        <div class="input-field col s12 m6 offset-m3">
            <button class="btn waves-effect waves-light green" type="submit" name="action">${tr.login_form_submit}
                <i class="material-icons right">send</i>
            </button>
        </div>

    </div>
    </form>

</div>

<!--JavaScript at end of body for optimized loading-->
<#include "../swap-files/script-imports.ftl">
</body>
</html>