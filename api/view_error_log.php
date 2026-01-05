<?php
/**
 * View Error Log - Shows recent PHP and Apache errors
 * Access: http://localhost/onlinebidding/api/view_error_log.php
 */

header('Content-Type: text/html; charset=utf-8');
?>
<!DOCTYPE html>
<html>
<head>
    <title>Error Log Viewer</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 1200px; margin: 20px auto; padding: 20px; background: #f5f5f5; }
        .container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #333; border-bottom: 2px solid #FF6B6B; padding-bottom: 10px; }
        .log-section { margin: 20px 0; }
        .log-content { background: #1e1e1e; color: #d4d4d4; padding: 15px; border-radius: 4px; overflow-x: auto; font-family: 'Courier New', monospace; font-size: 12px; max-height: 600px; overflow-y: auto; }
        .log-entry { margin: 5px 0; padding: 5px; border-left: 3px solid #4CAF50; padding-left: 10px; }
        .error-entry { border-left-color: #f44336; }
        .warning-entry { border-left-color: #ff9800; }
        .info-entry { border-left-color: #2196F3; }
        .btn { background: #4CAF50; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; text-decoration: none; display: inline-block; margin: 5px; }
        .btn:hover { background: #45a049; }
        pre { white-space: pre-wrap; word-wrap: break-word; }
    </style>
</head>
<body>
    <div class="container">
        <h1>📋 Error Log Viewer</h1>
        
        <?php
        // Common error log locations
        $logFiles = [
            'Apache Error Log' => 'C:\\xampp\\apache\\logs\\error.log',
            'PHP Error Log' => 'C:\\xampp\\php\\logs\\php_error_log',
            'PHP Error Log (Alt)' => ini_get('error_log'),
            'Apache Access Log' => 'C:\\xampp\\apache\\logs\\access.log'
        ];
        
        echo '<div class="log-section">';
        echo '<h2>Available Log Files:</h2>';
        echo '<ul>';
        foreach ($logFiles as $name => $path) {
            $exists = file_exists($path);
            $size = $exists ? filesize($path) : 0;
            $status = $exists ? '✅' : '❌';
            echo "<li>$status <strong>$name</strong>: $path";
            if ($exists) {
                echo " (" . number_format($size / 1024, 2) . " KB)";
            }
            echo "</li>";
        }
        echo '</ul>';
        echo '</div>';
        
        // Show Apache error log
        $apacheLog = 'C:\\xampp\\apache\\logs\\error.log';
        if (file_exists($apacheLog)) {
            echo '<div class="log-section">';
            echo '<h2>📄 Apache Error Log (Last 50 lines):</h2>';
            $lines = file($apacheLog);
            $recentLines = array_slice($lines, -50);
            echo '<div class="log-content">';
            foreach ($recentLines as $line) {
                $class = 'log-entry';
                if (stripos($line, 'error') !== false || stripos($line, 'fatal') !== false) {
                    $class .= ' error-entry';
                } elseif (stripos($line, 'warning') !== false) {
                    $class .= ' warning-entry';
                } else {
                    $class .= ' info-entry';
                }
                echo '<div class="' . $class . '">' . htmlspecialchars($line) . '</div>';
            }
            echo '</div>';
            echo '</div>';
        } else {
            echo '<div class="log-section">';
            echo '<p>❌ Apache error log not found at: ' . htmlspecialchars($apacheLog) . '</p>';
            echo '</div>';
        }
        
        // Show PHP error log
        $phpLog = 'C:\\xampp\\php\\logs\\php_error_log';
        if (file_exists($phpLog)) {
            echo '<div class="log-section">';
            echo '<h2>📄 PHP Error Log (Last 50 lines):</h2>';
            $lines = file($phpLog);
            $recentLines = array_slice($lines, -50);
            echo '<div class="log-content">';
            foreach ($recentLines as $line) {
                $class = 'log-entry';
                if (stripos($line, 'error') !== false || stripos($line, 'fatal') !== false) {
                    $class .= ' error-entry';
                } elseif (stripos($line, 'warning') !== false) {
                    $class .= ' warning-entry';
                } else {
                    $class .= ' info-entry';
                }
                echo '<div class="' . $class . '">' . htmlspecialchars($line) . '</div>';
            }
            echo '</div>';
            echo '</div>';
        } else {
            echo '<div class="log-section">';
            echo '<p>ℹ️ PHP error log not found at: ' . htmlspecialchars($phpLog) . '</p>';
            echo '<p>PHP errors might be logged to: ' . htmlspecialchars(ini_get('error_log')) . '</p>';
            echo '</div>';
        }
        
        // Show recent reset-password related errors
        echo '<div class="log-section">';
        echo '<h2>🔍 Recent Reset Password Errors:</h2>';
        echo '<div class="log-content">';
        
        $allLogs = [];
        if (file_exists($apacheLog)) {
            $allLogs = array_merge($allLogs, file($apacheLog));
        }
        if (file_exists($phpLog)) {
            $allLogs = array_merge($allLogs, file($phpLog));
        }
        
        $resetPasswordErrors = [];
        foreach ($allLogs as $line) {
            if (stripos($line, 'reset-password') !== false || 
                stripos($line, 'password reset') !== false ||
                stripos($line, 'Reset Password') !== false) {
                $resetPasswordErrors[] = $line;
            }
        }
        
        if (!empty($resetPasswordErrors)) {
            $recentErrors = array_slice($resetPasswordErrors, -20);
            foreach ($recentErrors as $line) {
                $class = 'log-entry error-entry';
                echo '<div class="' . $class . '">' . htmlspecialchars($line) . '</div>';
            }
        } else {
            echo '<p>No reset password related errors found in logs.</p>';
        }
        
        echo '</div>';
        echo '</div>';
        ?>
        
        <hr>
        <p><a href="view_error_log.php" class="btn">🔄 Refresh Logs</a></p>
        <p><small>This page shows the last 50 lines from each log file. Check the full log files for complete history.</small></p>
    </div>
</body>
</html>

