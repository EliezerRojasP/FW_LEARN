<?php
require __DIR__.'/bootstrap.php';method('POST'); user();query('DELETE FROM sesiones WHERE token_hash=?',[hash('sha256',bearer())]);respond(null,'Sesión cerrada.');
