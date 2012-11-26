<?xml version="1.0" encoding="UTF-8"?>
<tagSuggestions>
<#if types?exists>
<#list types?keys as type>
<type name="${type}">
<#list types[type] as entity>
    <tag>
        <name>${entity.name}</name>
        <nameURI>${entity.href}</nameURI>
        <relevance>${entity.relevance}</relevance>
        <latitude></latitude>
        <longitude></longitude>
        <website></website>
    </tag>
</#list>
</type>
</#list>
</#if>
</tagSuggestions>
