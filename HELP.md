# GraphQL Postman Query

```json
query SearchEmployee {
    searchEmployee(
        filterCriteria: [
            {
                key: "name"
                filterInput: {
                    filterType: eqIgnoreCase
                    filterValues: ["amsidh", "aditya", "adithi"]
                }
            }
        ]
        offset: 0
        limit: 10
        sortBy: { key: "name", order: ASC }
    ) {
        id
        name
        emailId
    }
}

mutation SaveEmployee {
    saveEmployee(saveEmployee: { name: "Amsidh", emailId: "amsidh@gmail.com" }) {
        id
        name
        emailId
    }
}
