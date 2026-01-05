<?php
/**
 * Email Configuration for PHPMailer
 * 
 * IMPORTANT: 
 * - Replace with your actual email credentials
 * - For Gmail: Use App Password (not your regular password)
 * - Keep this file secure (don't commit to public repos)
 */

return [
    // Email Provider Settings
    'smtp_host' => 'smtp.gmail.com',        // Gmail: smtp.gmail.com
    'smtp_port' => 587,                      // Gmail TLS: 587, SSL: 465
    'smtp_secure' => 'tls',                  // 'tls' or 'ssl'
    'smtp_auth' => true,                     // true for most providers
    
    // Your Email Credentials
    'smtp_username' => 'harsha168656@gmail.com',  // Your Gmail address
    'smtp_password' => 'iydhbbcypeowfnhq',     // Gmail App Password (spaces removed)
    
    // Email Details
    'from_email' => 'harsha168656@gmail.com',     // From email address
    'from_name' => 'Online Bidding',            // From name
    'reply_to_email' => 'harsha168656@gmail.com', // Reply-to email
    'reply_to_name' => 'Online Bidding Support', // Reply-to name
    
    // Debug Mode (set to false in production)
    'debug' => 2,                         // Set to 0, 2, or 3 for debugging (2 = client and server messages)
];

