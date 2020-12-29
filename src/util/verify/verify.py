import boto3
import json
from botocore.exceptions import ClientError

sqs = boto3.client('sqs')


# {
#    "verification_id":"xxx",
#    "subject_photo_id_key":"xxx",
#    "subject_photo_key":"xxx"
# }

# {
#    "verification_id":"xxx",
#    "verified":true/false
# }

def lambda_handler(message, context):
    print(json.dumps(message))
    verification_id = json.loads(message["Records"][0]["body"])["id"]
    id = json.loads(message["Records"][0]["body"])["idPhoto"]
    image = 'verifications/'+json.loads(message["Records"][0]["body"])["idPhoto"]
    print("id:",id,"image:",image)
    client = boto3.client('rekognition')
    verified = False

    try:
        rekognitionResponse = client.compare_faces(
            SourceImage={
                "S3Object": {
                    "Bucket": os.environ['BUCKET_NAME'],
                    "Name": id
                }
            },
            TargetImage={
                "S3Object": {
                    "Bucket": os.environ['BUCKET_NAME'],
                    "Name": image
                }
            },
            SimilarityThreshold=90,

        )
        if len(rekognitionResponse["FaceMatches"]) > 0:
            verified = True
        else:
            verified = False
    except ClientError as e:
        verified = False

    sqs_url = os.environ['NOTIFY_QUEUE_URL']
    response = sqs.send_message(
        QueueUrl=sqs_url,
        MessageBody=(json.dumps({'message_type':'response','entity_id':'OKTANK','requester_email':'samfolke@amazon.com','subject_name':'Samuel Folkes','verification_id': verification_id, 'verified':verified}))
    )
    print(json.dumps((json.dumps({'verification_id': verification_id, 'verified':verified}))))
    return {
        'statusCode': 200,
        'body':'success'
    }


