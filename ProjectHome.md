**Update** 11/8/2012 Note: now have an integration with Apache Stanbol download (prototype)

## 1. New home for Original OpenCalais Integration for Alfresco ##

(moved from Alfresco Forge)

Note: OpenCalais Integration supports Alfresco 3.3, 3.4, and 4.0, 4.2

Uses OpenCalais service to automatically extract semantic metadata. Auto-tagging
action can be used in rules. Has REST web scripts. Has Share UI: semantic tag cloud and geo-tagged map dashlets, auto-tag action. Also FlexSpaces UI has: tag clouds, map, suggest tags, tag editing.

[Alfresco OpenCalais Integration Share UI](http://integratedsemantics.org/2011/03/08/alfresco-opencalais-integration-share-ui/)

[![](http://integratedsemantics.org/wp-content/uploads/2011/03/share-calais-dashlets-2.png)](http://integratedsemantics.org/2011/03/08/alfresco-opencalais-integration-share-ui/)

![![](http://integratedsemantics.org/wp-content/uploads/2011/03/share-calais-autotag-action-2.png)](http://integratedsemantics.org/wp-content/uploads/2011/03/share-calais-autotag-action-1.png)


FlexSpaces (and FlexibleShare) Flex UI for Alfresco OpenCalais Integration:

> [![](http://integratedsemantics.org/wp-content/uploads/2008/12/geotag2_img.thumbnail.jpg)](http://integratedsemantics.org/wp-content/uploads/2008/12/geotag2.swf)

> [FlexSpaces Alfresco + Calais Integration](http://integratedsemantics.org/2008/12/08/calais-integration-for-alfresco-geo-tagging-flexspaces-part-2/)


## 2. Integration with [Apache Stanbol](http://stanbol.apache.org) ##

Initially has the same features as the OpenCalais integration (auto-tag action, semantic tag clouds, semantic geo-tagged map, webscripts).

Note: Stanbol Integration supports Alfresco 4.0 and 4.2

Leverages a Java client API library contributed to Stanbol by Zaizi that makes REST calls to Stanbol:

> [apache-stanbol-client github](https://github.com/efoncubierta/apache-stanbol-client)

> [jira with contrib](https://issues.apache.org/jira/browse/STANBOL-254) (used packages in the jira attached code, will need to change to what is in github if this jira issue doesn't make it in)

Note: Stanbol integration is only supported currently with Share. FlexSpaces hasn't been updated to also use the webscript URLs of this Stanbol integration (and have a preference option choose which to use).

Note: By default Stanbol uses OpenNLP, but can it can be configured to chain together different enhancement engines/services, including OpenCalais.

Also see:

> [Alfresco](http://www.alfresco.com/)

> [Integrated Semantics Blog](http://www.integratedsemantics.org)

> [Integrated Semantics](http://www.integratedsemantics.com)

> [OpenCalais Integration Alfresco Add+Ons page](https://addons.alfresco.com/addons/opencalais-integration)

> [FlexSpaces on Google Code](http://code.google.com/p/flexspaces/)

> [FlexibleShare on Google Code](http://code.google.com/p/flexibleshare/)

> [Apache Stanbol](http://stanbol.apache.org/)

> [IKS project](http://www.iks-project.eu/)

> [IKS project on Google Code](http://code.google.com/p/iks-project/)

> [OpenCalais](http://www.opencalais.com/)

> [Zaizi](http://www.zaizi.com/)