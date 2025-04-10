class Sector:
    def __init__(self, name: str, x_sx: float, y_sx: float, x_dx: float, y_dx: float, is_stage: bool):
        self.name = name
        self.x_sx = x_sx
        self.y_sx = y_sx
        self.x_dx = x_dx
        self.y_dx = y_dx
        self.is_stage = is_stage
        self.seats = []