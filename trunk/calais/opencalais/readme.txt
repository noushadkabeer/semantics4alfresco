12/6/08

To install calaisIntegration.amp
1. shutdown alfresco server
2. copy calaisIntegration.amp to <install_dir>/amps
3. run <install_dir>/apply_amps.bat  (pressing any key couple of times)
4. restart alfresco server

This installs:
1. calais integration auto tagging service
2. web client auto tagging action dialog for web client
3. calais integration webscripts

(Also see FlexSpaces for additional UI using this Alfresco Calais integration)


To use the open api Calais service you need to get an api key by registering at:
http://opencalais.com/user/register

Notes:
1. Open Calais 4.2 service supports processing Engish, French, and Spanish content. Check at www.opencalais.com for latest info.
2. If the content is too short, Calais may not recognize what language the content is in.
3. The 1.2 version of the Calais Integration supports Alfresco 3.3+ The older 1.1 version of the Calais Integration supports older Alfresco servers (2.1 - 3.2.x)






