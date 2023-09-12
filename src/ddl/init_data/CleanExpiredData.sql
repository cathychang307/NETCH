CREATE PROCEDURE CleanExpiredData
AS

DECLARE @ExpiredDate varchar(8)

SET @ExpiredDate = CONVERT(varchar (8), DATEADD([month], -3, GETDATE()) ,112)

DELETE FROM [InquiryLog]
WHERE  inquiry_date < @ExpiredDate
