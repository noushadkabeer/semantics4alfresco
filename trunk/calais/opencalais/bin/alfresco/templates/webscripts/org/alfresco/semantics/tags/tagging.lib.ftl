<#macro tagJSON item>
{
   "name" : "${jsonUtils.encodeJSONString(item.name)}",
   "count" : ${item.count?c},

   "latitude" : "${item.latitude}",
   
   "longitude" : "${item.longitude}"

}</#macro>
