def materials_form(material_link):
    if 'youtu' in material_link:
        return {'video':material_link}
    elif 'github' in material_link:
        return {'repo':material_link}
    elif 'ctfas' in material_link:
        return {'presentation':''.join(('https:',material_link))}
    else:
        return {'article':''.join(('https://2019.jpoint.ru',material_link,'/'))}
        