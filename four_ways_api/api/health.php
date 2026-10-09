<?php
require __DIR__.'/bootstrap.php';
method('GET'); query('SELECT 1'); respond(['estado'=>'ok','version'=>'2.1']);
