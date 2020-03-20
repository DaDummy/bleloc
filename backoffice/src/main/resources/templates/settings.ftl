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
<#assign title=tr.title_settings>
<#include "swap-files/top-bar.ftl">

<div class="container">

    <form method="POST" action="/backoffice/backoffice/settings/set">

    <#list settings as group>
        <div class="section">
                <h5>${tr["settings_group_"+group.title]}</h5>
                <div class="divider"></div>
                <div class="col">
                    <#list group.settings as setting>
                        <div class="row">
                            <div class="settings col s10">
                                <h5 class="grey-text">
                                    ${setting.label}
                                    <sup>
                                        <i class="material-icons small" title="${setting.description}">
                                            info
                                        </i>
                                    </sup>
                                </h5>
                            </div>
                            <div class="col s2">
                                <#if setting.type == "NUMBER">
                                    <input type="number" name="${setting.textId}" class="validate" value="${setting.value}">
                                <#elseif setting.type == "TEXT">
                                    <input type="text" name="${setting.textId}" class="validate" value="${setting.value}">
                                <#elseif setting.type == "BOOLEAN">
                                    <div class="switch">
                                        <label>
                                            <input type="hidden" name="${setting.textId}" value="">
                                            <input type="checkbox" name="${setting.textId}" <#if setting.value == "true">checked</#if>>
                                            <span class="lever"></span>
                                        </label>
                                    </div>
                                <#else>
                                    Unknown setting type
                                </#if>
                            </div>
                        </div>
                    </#list>
                </div>
            </div>
    </#list>

    <button class="waves-effect waves-light btn green" type="submit">Save Settings</button>

    </form>

</div>

<!--JavaScript at end of body for optimized loading-->
<#include "swap-files/script-imports.ftl">
</body>
</html>
