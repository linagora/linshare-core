function countCheckbox(grp)
{
    var result = $$('.'+grp).any(function(e) { return e.checked })?"block":"none";
}

function checkAll(grp, cb)
{
    $$('.'+grp).each(function(i){ i.checked = cb.checked });
    countCheckbox(grp);
}
