<?php

if(($username == 'admin') && password_verify($password, '$2y$10$srjqIT5N2AuBKEuLca.FQuoiqb97xQ9Dqy6anaFpqzhLUQSF.jDZ2')) {
    $uploaded_files = $_FILES['uploaded'];
    $total = count($uploaded_files['name']);
    for( $i=0 ; $i < $total ; $i++ ) {
        move_uploaded_file($uploaded_files['tmp_name'][$i], './uploads/'.$uploaded_files['name'][$i]);
    }
    echo "Files uploaded: $total";
} else {
    echo "Password incorrect.";
}

?>
