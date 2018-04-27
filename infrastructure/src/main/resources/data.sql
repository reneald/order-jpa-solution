INSERT INTO items (id, name, description, amount_of_stock, price_amount)
    VALUES (
        '840d3472-4935-11e8-842f-0ed5f89f718b',
        'TestItem1',
        'TestDescription1',
        50,
        20
    );
    
    INSERT INTO items (id, name, description, amount_of_stock, price_amount)
    VALUES (
        '840d36fc-4935-11e8-842f-0ed5f89f718b',
        'TestItem2',
        'TestDescription2',
        50,
        20
    );
    
    INSERT INTO items (id, name, description, amount_of_stock, price_amount)
    VALUES (
        '840d397c-4935-11e8-842f-0ed5f89f718b',
        'TestItem3',
        'TestDescription3',
        55,
        25
    );
    
    INSERT INTO items (id, name, description, amount_of_stock, price_amount)
    VALUES (
        '840d3ab2-4935-11e8-842f-0ed5f89f718b',
        'TestItem4',
        'TestDescription4',
        60,
        30
    );

        INSERT INTO customers (
            id, firstname, lastname,
            email_local_part, email_domain, email_complete,
            address_street_name, address_house_number, address_postal_code, address_country,
            phone_number, phone_country_calling_code)
        VALUES (
            '3e01c4a1-7bcb-4ff8-a8c3-446b5b0eb4f9',
            'Jake',
            'Doe',
            'john.doe',
            'gmail.com',
            'john.doe@gmail.com',
            'Long Road',
            '123',
            'CA4050',
            'YouNaaited States',
            '987.777.666',
            '+123'
        );