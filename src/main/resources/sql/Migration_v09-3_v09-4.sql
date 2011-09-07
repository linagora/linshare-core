-- Update default parameters stored in linshare_parameter table : Disable guest can create guest. 
UPDATE linshare_parameter
   SET guest_can_create_other=false;
       
