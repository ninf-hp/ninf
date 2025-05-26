<?
$filename = "count.dat";

$file = fopen($filename, "r");
$count = fread($file, filesize($filename));
fclose($file);

$count = $count + 1;
print("$count");

$file = fopen($filename, "w");
fwrite($file, $count);
fclose($file);
?>
