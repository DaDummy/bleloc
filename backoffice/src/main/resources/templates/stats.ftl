<!DOCTYPE html>
<#import "lang/translations_en.ftl" as tr>
<html lang="${.lang}">
<head>
    <meta charset="UTF-8">
    <title>BLELoc Admin Interface</title>
    <#include "swap-files/css-imports.ftl">

    <!--Let browser know website is optimized for mobile-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

    <script src="/backoffice/js/plotly-latest.min.js"></script>
</head>

<body>
<#assign title=tr.title_statistics>
<#include "swap-files/top-bar.ftl">

<div class="container">
    <div class="section">
	<#list graphs as g>
	<h5>${g.name}</h5>
	<div id="graph${g?index}"></div>
	<script>
		Plotly.plot("graph${g?index}", [
		    <#list g.curves as c>
		        {
		            name: "${c.name}",
		            x: [<#list c.points as p>${p.x?c}<#sep>,</#list>],
		            y: [<#list c.points as p>${p.y?c}<#sep>,</#list>]
		        }
		        <#sep>,
		    </#list>
		],{
			margin: { t: 50 },
			xaxis: {'title': '${g.xaxis}', 'autorange': 'true'},
			yaxis: {'title': '${g.yaxis}', 'autorange': 'true'},
		},{
			responsive: true,
		});
	</script>
	</#list>
    </div>
</div>

<!--JavaScript at end of body for optimized loading-->
<#include "swap-files/script-imports.ftl">
</body>
</html>
