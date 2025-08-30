export class DetailedError extends Error {
    details?: object

    constructor(message: string, details?: object) {
        super(message)
        this.name = 'DetailedError'
        Object.setPrototypeOf(this, DetailedError.prototype)

        this.details = details
    }

    override toString() {
        let message = `${this.name}: ${this.message}`

        if (this.details) {
            message += 'Error Details:\n'
            message += Object.entries(this.details).map(
                entry => `${entry[0]}: ${entry[1]}`
            ).join('\n')
        }

        return message;
    }
}