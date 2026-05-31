$ErrorActionPreference = "Stop"
$WorkingDir = "c:\Users\AfaqAhmed\Desktop\New folder (7)"
cd $WorkingDir

# Function to get Base64 of image
function Get-Base64Image($FilePath) {
    if (Test-Path $FilePath) {
        $Bytes = [System.IO.File]::ReadAllBytes($FilePath)
        $Base64 = [System.Convert]::ToBase64String($Bytes)
        return "data:image/png;base64,$Base64"
    } else {
        return ""
    }
}

Write-Host "Converting images to base64..."

# Screenshots
$DashFull = Get-Base64Image "soc_dashboard_full_1780173885025.png"
$DashLower = Get-Base64Image "soc_dashboard_lower_1780174016372.png"

# UML diagrams
$UmlClass = Get-Base64Image "docs\uml\SDA-Pro_Class_Diagram.png"
$UmlComp = Get-Base64Image "docs\uml\SDA-Pro_Component_Diagram.png"
$UmlSeqIngest = Get-Base64Image "docs\uml\SDA-Pro_Sequence_Alerts.png"
$UmlSeqResp = Get-Base64Image "docs\uml\SDA-Pro_Sequence_Response.png"

Write-Host "Bulding HTML..."

$HtmlContent = @"
<html xmlns:o="urn:schemas-microsoft-com:office:office"
      xmlns:w="urn:schemas-microsoft-com:office:word"
      xmlns="http://www.w3.org/TR/REC-html40">
<head>
<meta charset="utf-8">
<style>
  body { font-family: "Calibri", "Segoe UI", sans-serif; line-height: 1.6; color: #000; }
  .cover-page { text-align: center; margin-top: 150px; page-break-after: always; }
  .project-title { font-size: 28pt; font-weight: bold; color: #2c3e50; margin-bottom: 20px; }
  .course-title { font-size: 16pt; margin-bottom: 50px; }
  .team-members { font-size: 14pt; font-weight: bold; margin-top: 50px; text-align: center; }
  .team-table { margin: 0 auto; width: 60%; margin-top: 20px; }
  .team-table td { padding: 10px; border: none; font-size: 14pt; font-weight: bold; text-align: center; }
  h1 { color: #2980b9; font-size: 18pt; border-bottom: 2px solid #3498db; padding-bottom: 5px; margin-top: 30px; page-break-before: always; }
  h2 { color: #2c3e50; font-size: 14pt; margin-top: 20px; }
  p { font-size: 11pt; text-align: justify; margin-bottom: 12px; }
  ul { font-size: 11pt; margin-bottom: 15px; }
  li { margin-bottom: 5px; }
  .diagram-img { max-width: 100%; height: auto; border: 1px solid #ccc; margin: 15px 0; }
  .caption { text-align: center; font-size: 9pt; font-style: italic; color: #7f8c8d; margin-top: -10px; margin-bottom: 20px; }
</style>
</head>
<body>

<div class="cover-page">
  <div class="project-title">SDA-Pro: Intelligent SOC Platform</div>
  <div class="course-title">Software Design and Architecture<br>Semester Project Final Submission</div>
  <div class="team-members">Submitted by:</div>
  <table class="team-table">
    <tr><td>Afaq Ahmed</td></tr>
    <tr><td>Haseeb ur Rehman</td></tr>
    <tr><td>Farhan ul Haq</td></tr>
  </table>
</div>

<h1>1. Executive Summary</h1>
<p>The <strong>SDA-Pro Intelligent Security Operations Center (SOC) Platform</strong> is a comprehensive, distributed enterprise security application designed as a semester project to demonstrate sophisticated software architecture and design patterns. The project fulfills all functional, architectural, and structural requirements outlined in the official course proposal.</p>
<p>The core objective of the platform is to ingest security alerts from disparate sources, normalize and enrich them through a centralized pipeline, correlate them into actionable security incidents, and execute automated response orchestration—all visualized in a robust real-time web dashboard.</p>

<h1>2. Live Dashboard Working Demonstration</h1>
<p>The following screenshots showcase the functioning SOC Dashboard connected to the event-stream pipelines, verifying end-to-end integration of the MVC architecture and Observer events.</p>
<img class="diagram-img" src="$DashFull" />
<div class="caption">Figure 1: Full View of the SDA-Pro Dashboard showing Alerts, Metrics, and Incident Queue</div>
<img class="diagram-img" src="$DashLower" />
<div class="caption">Figure 2: Response Actions Console demonstrating State and Strategy responses</div>

<h1>3. Architectural Styles</h1>
<p>The platform successfully models and implements four distinct architectural paradigms to ensure scalable and maintainable operations:</p>
<ul>
  <li><strong>Service-Oriented Architecture (SOA):</strong> Decomposed into 8 autonomous microservices (Alert Ingestion, Enrichment, Incident Management, Response Orchestration, Threat Intel, Notification, Audit, and Identity).</li>
  <li><strong>Event-Driven Architecture (Observer):</strong> Central to the system is a RabbitMQ message bus decoupling the services. Domain events act as triggers for stateless consumers, ensuring loose coupling.</li>
  <li><strong>Model-View-Controller (MVC):</strong> The front-end SOC interface is built using reactive components where controllers handle business operations, models manage API state via Server-Sent Events, and views render the DOM structure dynamically.</li>
  <li><strong>Layered Architecture:</strong> Each individual service maintains strict internal stratification with distinct Presentation (Controllers), Business Logic (Services), and Data Access (Repositories) layers natively coded in Java.</li>
</ul>

<h1>4. Design Pattern Implementations</h1>
<p>The project rigorously integrates 12 Gang of Four (GoF) design patterns to solve common architectural challenges seamlessly. These implementations are mapped explicitly within the system components:</p>

<h2>4.1 Complete Structural Class Diagram</h2>
<p>The following UML Class Diagram visualizes the implementation of the design patterns specifically across the domain components, illustrating Composite alert groupings, the State-based incident machine, and the Chain of Responsibility pipeline.</p>
<img class="diagram-img" src="$UmlClass" />
<div class="caption">Figure 3: System Class Diagram depicting GoF Patterns (Composite, State, Factory, Singleton, Strategy)</div>

<h2>4.2 Service & Component Interactions (SOA)</h2>
<p>The containerized microservice interdependencies are demonstrated below, capturing the layered separation of internal REST/gRPC queries versus asynchronous bus events.</p>
<img class="diagram-img" src="$UmlComp" />
<div class="caption">Figure 4: SOA Component Diagram showcasing explicit independent service borders</div>

<h1>5. End-to-End System Workflows</h1>
<p>The project features full end-to-end simulated security response flows mapped to precision in sequence logic.</p>

<h2>5.1 Alert Ingestion & Enrichment Pipeline</h2>
<p>Incoming webhooks are normalized into canonical alert schemas, grouped by origin, and processed by the Chain of Responsibility to enrich threat intelligence variables. Once processed, an incident object transitions onto the state machine starting in <strong>NewState</strong> and triggering an <code>IncidentCreated</code> bus event.</p>
<img class="diagram-img" src="$UmlSeqIngest" />
<div class="caption">Figure 5: Alert Sequence depicting Webhook ingestion to Enrichment correlation</div>

<h2>5.2 Response Orchestration & execution</h2>
<p>When an analyst commands a response (e.g., Endpoint Isolation), the Facade validates execution rights via Proxy, chains a Decorator to securely drop an immutable compliance payload into the Audit service, and executes the appropriate mitigation Strategy.</p>
<img class="diagram-img" src="$UmlSeqResp" />
<div class="caption">Figure 6: Response Orchestration Sequence demonstrating Facade, Decorator, and Strategy coordination</div>

<h1>6. Conclusion</h1>
<p>The SDA-Pro Intelligent SOC Platform successfully bridges academic design theory with modern DevOps practices. By implementing diverse components mapping directly to all 12 proposed patterns and 4 distributed architectures, the final build matches production-ready structures. The team successfully verified all requirements according to the design proposal.</p>

</body>
</html>
"@

$HtmlPath = "$WorkingDir\temp_submission.html"
$DocxPath = "$WorkingDir\SDA-Pro_Semester_Project_Submission_Final.docx"
[System.IO.File]::WriteAllText($HtmlPath, $HtmlContent)

Write-Host "Converting HTML to DOCX using Word COM Object..."
try {
    $word = New-Object -ComObject Word.Application
    $word.Visible = $false
    $doc = $word.Documents.Open($HtmlPath)
    # 16 = wdFormatDocumentDefault (docx)
    $doc.SaveAs($DocxPath, 16)
    $doc.Close()
    $word.Quit()
    Remove-Item $HtmlPath -Force
    Write-Host "Success! DOCX generation complete: $DocxPath"
} catch {
    Write-Host "Error converting with Word COM: $_"
    if ($word) { $word.Quit() }
}
