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
        <#if entity.resolutions?exists && (entity.resolutions?size > 0) >
          <#list entity.resolutions  as res>
            <normalized>${res.name!}</normalized>
            <latitude>${res.latitude!}</latitude>
            <longitude>${res.longitude!}</longitude>
            <website>${res.webaddress!}</website>
            <ticker>${res.ticker!}</ticker>
          </#list>    
        <#else>
          <normalized></normalized>
          <latitude></latitude>
          <longitude></longitude>
          <website></website>
          <ticker></ticker>
        </#if>
    </tag>
</#list>
</type>
</#list>
</#if>
</tagSuggestions>
