<?xml version="1.0" encoding="utf-8"?>
<serviceModel xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" name="MyRESTService.Azure" generation="1" functional="0" release="0" Id="20a8ae11-4316-4305-a8f5-a84ded24d2d7" dslVersion="1.2.0.0" xmlns="http://schemas.microsoft.com/dsltools/RDSM">
  <groups>
    <group name="MyRESTService.AzureGroup" generation="1" functional="0" release="0">
      <componentports>
        <inPort name="MyRESTService:Endpoint1" protocol="http">
          <inToChannel>
            <lBChannelMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/LB:MyRESTService:Endpoint1" />
          </inToChannel>
        </inPort>
        <inPort name="WebRole1:Endpoint1" protocol="http">
          <inToChannel>
            <lBChannelMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/LB:WebRole1:Endpoint1" />
          </inToChannel>
        </inPort>
      </componentports>
      <settings>
        <aCS name="MyRESTService:APPINSIGHTS_INSTRUMENTATIONKEY" defaultValue="">
          <maps>
            <mapMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/MapMyRESTService:APPINSIGHTS_INSTRUMENTATIONKEY" />
          </maps>
        </aCS>
        <aCS name="MyRESTService:Microsoft.WindowsAzure.Plugins.Diagnostics.ConnectionString" defaultValue="">
          <maps>
            <mapMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/MapMyRESTService:Microsoft.WindowsAzure.Plugins.Diagnostics.ConnectionString" />
          </maps>
        </aCS>
        <aCS name="MyRESTServiceInstances" defaultValue="[1,1,1]">
          <maps>
            <mapMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/MapMyRESTServiceInstances" />
          </maps>
        </aCS>
        <aCS name="WebRole1:Microsoft.WindowsAzure.Plugins.Diagnostics.ConnectionString" defaultValue="">
          <maps>
            <mapMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/MapWebRole1:Microsoft.WindowsAzure.Plugins.Diagnostics.ConnectionString" />
          </maps>
        </aCS>
        <aCS name="WebRole1Instances" defaultValue="[1,1,1]">
          <maps>
            <mapMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/MapWebRole1Instances" />
          </maps>
        </aCS>
      </settings>
      <channels>
        <lBChannel name="LB:MyRESTService:Endpoint1">
          <toPorts>
            <inPortMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/MyRESTService/Endpoint1" />
          </toPorts>
        </lBChannel>
        <lBChannel name="LB:WebRole1:Endpoint1">
          <toPorts>
            <inPortMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/WebRole1/Endpoint1" />
          </toPorts>
        </lBChannel>
      </channels>
      <maps>
        <map name="MapMyRESTService:APPINSIGHTS_INSTRUMENTATIONKEY" kind="Identity">
          <setting>
            <aCSMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/MyRESTService/APPINSIGHTS_INSTRUMENTATIONKEY" />
          </setting>
        </map>
        <map name="MapMyRESTService:Microsoft.WindowsAzure.Plugins.Diagnostics.ConnectionString" kind="Identity">
          <setting>
            <aCSMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/MyRESTService/Microsoft.WindowsAzure.Plugins.Diagnostics.ConnectionString" />
          </setting>
        </map>
        <map name="MapMyRESTServiceInstances" kind="Identity">
          <setting>
            <sCSPolicyIDMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/MyRESTServiceInstances" />
          </setting>
        </map>
        <map name="MapWebRole1:Microsoft.WindowsAzure.Plugins.Diagnostics.ConnectionString" kind="Identity">
          <setting>
            <aCSMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/WebRole1/Microsoft.WindowsAzure.Plugins.Diagnostics.ConnectionString" />
          </setting>
        </map>
        <map name="MapWebRole1Instances" kind="Identity">
          <setting>
            <sCSPolicyIDMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/WebRole1Instances" />
          </setting>
        </map>
      </maps>
      <components>
        <groupHascomponents>
          <role name="MyRESTService" generation="1" functional="0" release="0" software="C:\Users\brand\Desktop\Tuber\Server\MyRESTService\MyRESTService.Azure\csx\Release\roles\MyRESTService" entryPoint="base\x64\WaHostBootstrapper.exe" parameters="base\x64\WaIISHost.exe " memIndex="-1" hostingEnvironment="frontendadmin" hostingEnvironmentVersion="2">
            <componentports>
              <inPort name="Endpoint1" protocol="http" portRanges="80" />
            </componentports>
            <settings>
              <aCS name="APPINSIGHTS_INSTRUMENTATIONKEY" defaultValue="" />
              <aCS name="Microsoft.WindowsAzure.Plugins.Diagnostics.ConnectionString" defaultValue="" />
              <aCS name="__ModelData" defaultValue="&lt;m role=&quot;MyRESTService&quot; xmlns=&quot;urn:azure:m:v1&quot;&gt;&lt;r name=&quot;MyRESTService&quot;&gt;&lt;e name=&quot;Endpoint1&quot; /&gt;&lt;/r&gt;&lt;r name=&quot;WebRole1&quot;&gt;&lt;e name=&quot;Endpoint1&quot; /&gt;&lt;/r&gt;&lt;/m&gt;" />
            </settings>
            <resourcereferences>
              <resourceReference name="DiagnosticStore" defaultAmount="[4096,4096,4096]" defaultSticky="true" kind="Directory" />
              <resourceReference name="EventStore" defaultAmount="[1000,1000,1000]" defaultSticky="false" kind="LogStore" />
            </resourcereferences>
          </role>
          <sCSPolicy>
            <sCSPolicyIDMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/MyRESTServiceInstances" />
            <sCSPolicyUpdateDomainMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/MyRESTServiceUpgradeDomains" />
            <sCSPolicyFaultDomainMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/MyRESTServiceFaultDomains" />
          </sCSPolicy>
        </groupHascomponents>
        <groupHascomponents>
          <role name="WebRole1" generation="1" functional="0" release="0" software="C:\Users\brand\Desktop\Tuber\Server\MyRESTService\MyRESTService.Azure\csx\Release\roles\WebRole1" entryPoint="base\x64\WaHostBootstrapper.exe" parameters="base\x64\WaIISHost.exe " memIndex="-1" hostingEnvironment="frontendadmin" hostingEnvironmentVersion="2">
            <componentports>
              <inPort name="Endpoint1" protocol="http" portRanges="8080" />
            </componentports>
            <settings>
              <aCS name="Microsoft.WindowsAzure.Plugins.Diagnostics.ConnectionString" defaultValue="" />
              <aCS name="__ModelData" defaultValue="&lt;m role=&quot;WebRole1&quot; xmlns=&quot;urn:azure:m:v1&quot;&gt;&lt;r name=&quot;MyRESTService&quot;&gt;&lt;e name=&quot;Endpoint1&quot; /&gt;&lt;/r&gt;&lt;r name=&quot;WebRole1&quot;&gt;&lt;e name=&quot;Endpoint1&quot; /&gt;&lt;/r&gt;&lt;/m&gt;" />
            </settings>
            <resourcereferences>
              <resourceReference name="DiagnosticStore" defaultAmount="[4096,4096,4096]" defaultSticky="true" kind="Directory" />
              <resourceReference name="EventStore" defaultAmount="[1000,1000,1000]" defaultSticky="false" kind="LogStore" />
            </resourcereferences>
          </role>
          <sCSPolicy>
            <sCSPolicyIDMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/WebRole1Instances" />
            <sCSPolicyUpdateDomainMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/WebRole1UpgradeDomains" />
            <sCSPolicyFaultDomainMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/WebRole1FaultDomains" />
          </sCSPolicy>
        </groupHascomponents>
      </components>
      <sCSPolicy>
        <sCSPolicyUpdateDomain name="MyRESTServiceUpgradeDomains" defaultPolicy="[5,5,5]" />
        <sCSPolicyUpdateDomain name="WebRole1UpgradeDomains" defaultPolicy="[5,5,5]" />
        <sCSPolicyFaultDomain name="MyRESTServiceFaultDomains" defaultPolicy="[2,2,2]" />
        <sCSPolicyFaultDomain name="WebRole1FaultDomains" defaultPolicy="[2,2,2]" />
        <sCSPolicyID name="MyRESTServiceInstances" defaultPolicy="[1,1,1]" />
        <sCSPolicyID name="WebRole1Instances" defaultPolicy="[1,1,1]" />
      </sCSPolicy>
    </group>
  </groups>
  <implements>
    <implementation Id="7c2de353-4533-4186-bfc7-63712d17b85d" ref="Microsoft.RedDog.Contract\ServiceContract\MyRESTService.AzureContract@ServiceDefinition">
      <interfacereferences>
        <interfaceReference Id="ff8339de-fa16-4045-b6f0-ad251c9ae3f6" ref="Microsoft.RedDog.Contract\Interface\MyRESTService:Endpoint1@ServiceDefinition">
          <inPort>
            <inPortMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/MyRESTService:Endpoint1" />
          </inPort>
        </interfaceReference>
        <interfaceReference Id="2c466101-9681-4e0a-a49d-5b66e4a731f6" ref="Microsoft.RedDog.Contract\Interface\WebRole1:Endpoint1@ServiceDefinition">
          <inPort>
            <inPortMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/WebRole1:Endpoint1" />
          </inPort>
        </interfaceReference>
      </interfacereferences>
    </implementation>
  </implements>
</serviceModel>