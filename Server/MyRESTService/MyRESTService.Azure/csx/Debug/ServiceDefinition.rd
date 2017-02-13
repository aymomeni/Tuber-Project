<?xml version="1.0" encoding="utf-8"?>
<serviceModel xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" name="MyRESTService.Azure" generation="1" functional="0" release="0" Id="24f6d5af-611e-4043-b8eb-c7290f7ff8a7" dslVersion="1.2.0.0" xmlns="http://schemas.microsoft.com/dsltools/RDSM">
  <groups>
    <group name="MyRESTService.AzureGroup" generation="1" functional="0" release="0">
      <componentports>
        <inPort name="MyRESTService:Endpoint1" protocol="http">
          <inToChannel>
            <lBChannelMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/LB:MyRESTService:Endpoint1" />
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
      </settings>
      <channels>
        <lBChannel name="LB:MyRESTService:Endpoint1">
          <toPorts>
            <inPortMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/MyRESTService/Endpoint1" />
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
      </maps>
      <components>
        <groupHascomponents>
          <role name="MyRESTService" generation="1" functional="0" release="0" software="C:\Users\brand\Desktop\Tuber\Server\MyRESTService\MyRESTService.Azure\csx\Debug\roles\MyRESTService" entryPoint="base\x64\WaHostBootstrapper.exe" parameters="base\x64\WaIISHost.exe " memIndex="-1" hostingEnvironment="frontendadmin" hostingEnvironmentVersion="2">
            <componentports>
              <inPort name="Endpoint1" protocol="http" portRanges="80" />
            </componentports>
            <settings>
              <aCS name="APPINSIGHTS_INSTRUMENTATIONKEY" defaultValue="" />
              <aCS name="Microsoft.WindowsAzure.Plugins.Diagnostics.ConnectionString" defaultValue="" />
              <aCS name="__ModelData" defaultValue="&lt;m role=&quot;MyRESTService&quot; xmlns=&quot;urn:azure:m:v1&quot;&gt;&lt;r name=&quot;MyRESTService&quot;&gt;&lt;e name=&quot;Endpoint1&quot; /&gt;&lt;/r&gt;&lt;/m&gt;" />
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
      </components>
      <sCSPolicy>
        <sCSPolicyUpdateDomain name="MyRESTServiceUpgradeDomains" defaultPolicy="[5,5,5]" />
        <sCSPolicyFaultDomain name="MyRESTServiceFaultDomains" defaultPolicy="[2,2,2]" />
        <sCSPolicyID name="MyRESTServiceInstances" defaultPolicy="[1,1,1]" />
      </sCSPolicy>
    </group>
  </groups>
  <implements>
    <implementation Id="f5630b7e-4151-4414-9130-e885c915bc03" ref="Microsoft.RedDog.Contract\ServiceContract\MyRESTService.AzureContract@ServiceDefinition">
      <interfacereferences>
        <interfaceReference Id="44c8e1a0-50e6-4432-87b9-5220ea71786c" ref="Microsoft.RedDog.Contract\Interface\MyRESTService:Endpoint1@ServiceDefinition">
          <inPort>
            <inPortMoniker name="/MyRESTService.Azure/MyRESTService.AzureGroup/MyRESTService:Endpoint1" />
          </inPort>
        </interfaceReference>
      </interfacereferences>
    </implementation>
  </implements>
</serviceModel>